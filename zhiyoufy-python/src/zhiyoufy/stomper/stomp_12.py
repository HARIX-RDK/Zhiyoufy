"""
This is a python client implementation of the STOMP protocol.

It aims to be transport layer neutral. This module provides functions to
create and parse STOMP messages in a programmatic fashion.

The examples package contains two examples using twisted as the transport
framework. Other frameworks can be used and I may add other examples as
time goes on.

The STOMP protocol specification maybe found here:

 * http://stomp.codehaus.org/Protocol

I've looked at the stomp client by Jason R. Briggs and have based the message
generation on how his client does it. The client can be found at the follow
address however it isn't a dependancy.

 * http://www.briggs.net.nz/log/projects/stomppy

In testing this library I run against ActiveMQ project. The server runs
in java, however its fairly standalone and easy to set up. The projects
page is here:

 * http://activemq.apache.org/


(c) Oisin Mulvihill, 2007-07-26.
    Ralph Bean, 2014-09-09.
License: http://www.apache.org/licenses/LICENSE-2.0

"""
import logging
import uuid
import json

from .core import StompException

# This is used as a return from message responses functions.
# It is used more for readability more then anything or reason.
NO_RESPONSE_NEEDED = ""

# The version of the protocol we implement.
STOMP_VERSION = "1.2"

# Message terminator:
NULL = "\x00"

# STOMP Spec v1.1 valid commands:
VALID_COMMANDS = [
    "ABORT", "ACK", "BEGIN", "COMMIT",
    "CONNECT", "CONNECTED", "DISCONNECT", "MESSAGE",
    "NACK", "SEND", "SUBSCRIBE", "UNSUBSCRIBE",
    "RECEIPT", "ERROR",
]


def get_log():
    return logging.getLogger("stomper")


class Frame(object):
    """This class is used to create or read STOMP message frames.

    The method pack() is used to create a STOMP message ready
    for transmission.

    The method unpack() is used to read a STOMP message into
    a frame instance. It uses the unpack_frame(...) function
    to do the initial parsing.

    The frame has three important member variables:

      * cmd
      * headers
      * body

    The 'cmd' is a property that represents the STOMP message
    command. When you assign this a check is done to make sure
    its one of the VALID_COMMANDS. If not then StompException will
    be raised.

    The 'headers' is a dictionary which the user can added to
    if needed. There are no restrictions or checks imposed on
    what values are inserted.

    The 'body' is just a member variable that the body text
    is assigned to.

    """
    def __init__(self):
        """Setup the internal state."""
        self._cmd = ""
        self.body = ""
        self.headers = {}

    @property
    def cmd(self):
        """Don't use _cmd directly!"""
        return self._cmd

    @cmd.setter
    def cmd(self, cmd):
        """Check the cmd is valid, StompException will be raised if its not."""
        cmd = cmd.upper()
        if cmd not in VALID_COMMANDS:
            raise StompException("The cmd '%s' is not valid! It must be one of '%s' (STOMP v%s)." % (
                cmd, VALID_COMMANDS, STOMP_VERSION)
            )
        else:
            self._cmd = cmd

    def pack(self):
        """Called to create a STOMP message from the internal values.
        """
        headers = "".join(
            ["%s:%s\n" % (f, v) for f, v in self.headers.items()]
        )
        stomp_message = "%s\n%s\n%s%s" % (self._cmd, headers, self.body, NULL)

        return stomp_message

    def pack_json_binary(self):
        stomp_message_binary = self._cmd.encode("utf-8") + b"\n"

        for k, v in self.headers.items():
            stomp_message_binary += k.encode("utf-8") + b":" + v.encode("utf-8") + b"\n"

        if isinstance(self.body, dict):
            body_json = json.dumps(self.body)
            body_encoded = body_json.encode("utf-8")
        else:
            body_encoded = self.body.encode("utf-8")

        stomp_message_binary += b"content-type:application/json\n"
        stomp_message_binary += f"content-length:{len(body_encoded)}\n".encode("utf-8")
        stomp_message_binary += b"\n"

        stomp_message_binary += body_encoded + b"\x00"

        return stomp_message_binary

    def to_json(self):
        return {
            "cmd": self._cmd,
            "headers": self.headers,
            "body": self.body,
        }


def abort(transaction_id):
    """STOMP abort transaction command.

    Rollback whatever actions in this transaction.

    transaction_id:
        This is the id that all actions in this transaction.

    """
    headers = f"transaction:{transaction_id}\n"

    message = "ABORT\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def ack(ack_id, transaction_id=None):
    """STOMP acknowledge command.

    Acknowledge receipt of a specific message from the server.

    ack_id:
        The ACK frame MUST include an id header matching the ack header
        of the MESSAGE being acknowledged

    transaction_id:
        Optionally, a transaction header MAY be specified, indicating that
        the message acknowledgment SHOULD be part of the named transaction

    """
    headers = f"id:{ack_id}\n"

    if transaction_id:
        headers += f"transaction:{transaction_id}\n"

    message = "ACK\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def nack(message_id, subscription_id, transaction_id=None):
    """STOMP negative acknowledge command.

    NACK is the opposite of ACK. It is used to tell the server that the client
    did not consume the message. The server can then either send the message to
    a different client, discard it, or put it in a dead letter queue. The exact
    behavior is server specific.

    message_id:
        This is the id of the message we are acknowledging,
        what else could it be? ;)

    subscription_id:
        This is the id of the subscription that applies to the message.

    transaction_id:
        This is the id that all actions in this transaction
        will have. If this is not given then a random UUID
        will be generated for this.

    """
    headers = f"subscription:{subscription_id}\n"
    headers += f"message-id:{message_id}\n"

    if transaction_id:
        headers += f"transaction:{transaction_id}\n"

    message = "NACK\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def begin(transaction_id=None):
    """STOMP begin command.

    Start a transaction...

    transaction_id:
        This is the id that all actions in this transaction
        will have. If this is not given then a random UUID
        will be generated for this.

    """
    if not transaction_id:
        # Generate a random UUID:
        transaction_id = uuid.uuid4()

    headers = f"transaction:{transaction_id}\n"

    message = "BEGIN\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def commit(transaction_id):
    """STOMP commit command.

    Do whatever is required to make the series of actions
    permanent for this transaction_id.

    transaction_id:
        This is the id that all actions in this transaction.

    """
    headers = f"transaction:{transaction_id}\n"

    message = "COMMIT\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def connect(username, password, host, heartbeats=(0,0)):
    """STOMP connect command.

    username, password:
        These are the needed auth details to connect to the
        message server.

    After sending this we will receive a CONNECTED
    message which will contain our session id.

    """
    if len(heartbeats) != 2:
        raise ValueError("Invalid heartbeat %r" % heartbeats)
    cx, cy = heartbeats

    headers = f"accept-version:1.1\n"
    headers += f"host:{host}\n"
    headers += f"heart-beat:{cx},{cy}\n"
    headers += f"login:{username}\n"
    headers += f"passcode:{password}\n"

    message = "CONNECT\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def disconnect(receipt=None):
    """STOMP disconnect command.

    Tell the server we finished and we'll be closing the
    socket soon.

    """
    if not receipt:
        receipt = uuid.uuid4()

    headers = f"receipt:{receipt}\n"

    message = "DISCONNECT\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def send(dest, msg, transaction_id=None, content_type="text/plain"):
    """STOMP send command.

    dest:
        This is the channel we wish to subscribe to

    msg:
        This is the message body to be sent.

    transaction_id:
        This is an optional field and is not needed
        by default.

    """
    headers = f"destination:{dest}\n"

    if transaction_id:
        headers += f"transaction:{transaction_id}\n"

    headers += f"content-type:{content_type}\n"
    headers += f"content-length:{len(msg)}\n"

    message = "SEND\n"
    message += headers
    message += "\n"
    message += msg
    message += NULL


def subscribe(dest, idx, ack="auto"):
    """STOMP subscribe command.

    dest:
        This is the channel we wish to subscribe to

    idx:
        The ID that should uniquely identify the subscription

    ack: 'auto' | 'client'
        If the ack is set to client, then messages received will
        have to have an acknowledge as a reply. Otherwise the server
        will assume delivery failure.

    """
    headers = f"id:{idx}\n"
    headers += f"destination:{dest}\n"
    headers += f"ack:{ack}\n"

    message = "SUBSCRIBE\n"
    message += headers
    message += "\n"
    message += NULL

    return message


def unsubscribe(idx):
    """STOMP unsubscribe command.

    idx:
        This is the id of the subscription

    Tell the server we no longer wish to receive any
    further messages for the given subscription.

    """
    headers = f"id:{idx}\n"

    message = "UNSUBSCRIBE\n"
    message += headers
    message += "\n"
    message += NULL

    return message


class Engine(object):
    """This is a simple state machine to return a response to received
    message if needed.

    """
    def __init__(self):
        self.log = logging.getLogger("stomper.Engine")

        self.session_id = None

        # Entry Format:
        #
        #    COMMAND : Handler_Function
        #
        self.command_handler_map = {
            "CONNECTED": self.connected,
            "MESSAGE": self.ack,
            "ERROR": self.error,
            "RECEIPT": self.receipt,
        }

    def react(self, msg):
        """Called to provide a response to a message if needed.

        msg:
            This is a dictionary as returned by unpack_frame(...)
            or it can be a straight STOMP message. This function
            will attempt to determine which an deal with it.

        returned:
            A message to return or an empty string.

        """
        returned = NO_RESPONSE_NEEDED

        if isinstance(msg, dict):
            pass
        else:
            raise StompException("Unknown message type '%s', I don't know what to do with this!" % type(msg))

        if msg["cmd"] in self.command_handler_map:
            returned = self.command_handler_map[msg["cmd"]](msg)

        return returned

    def connected(self, msg):
        """No response is needed to a connected frame.

        This method stores the session id as the
        member sessionId for later use.

        returned:
            NO_RESPONSE_NEEDED

        """
        self.session_id = msg["headers"].get("session", None)

        return NO_RESPONSE_NEEDED

    def ack(self, msg):
        """Called when a MESSAGE has been received.

        Override this method to handle received messages.

        This function will generate an acknowledge message
        for the given message and transaction (if present).

        """
        ack_id = msg["headers"].get("ack", None)

        if not ack_id:
            return NO_RESPONSE_NEEDED

        transaction_id = msg["headers"].get("transaction-id", None)

        return ack(ack_id, transaction_id)

    def error(self, msg):
        """Called to handle an error message received from the server.

        This method just logs the error message

        returned:
            NO_RESPONSE_NEEDED

        """
        body = msg["body"]

        brief_msg = msg["headers"].get("message", "")

        self.log.error("Received server error - message%s\n\n%s" % (brief_msg, body))

        return NO_RESPONSE_NEEDED

    def receipt(self, msg):
        """Called to handle a receipt message received from the server.

        This method just logs the receipt message

        returned:
            NO_RESPONSE_NEEDED

        """
        body = msg["body"]

        brief_msg = msg["headers"].get("receipt-id", "")

        self.log.info("Received server receipt message - receipt-id:%s\n\n%s" % (brief_msg, body))

        return NO_RESPONSE_NEEDED

