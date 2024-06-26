import smtplib
import mimetypes
import codecs
import time
from email import encoders
from email.mime.audio import MIMEAudio
from email.mime.base import MIMEBase
from email.mime.image import MIMEImage

from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
import logging as log


class MailUtil:
    def __init__(self, server, user, password, str_from=None, use_ssl=True):
        self.server = server
        self.user = user
        self.password = password
        if str_from:
            self.str_from = str_from
        else:
            self.str_from = user
        self.use_ssl = use_ssl

    def send_mail(self, str_to, str_subject, plain_text, html_text=None, attachments=None, try_num=1):
        outer = MIMEMultipart('alternative')
        outer['Subject'] = str_subject
        outer['From'] = self.str_from
        outer['To'] = str_to
        outer.preamble = 'You will not see this in a MIME-aware mail reader.\n'

        part1 = MIMEText(plain_text, 'plain')
        outer.attach(part1)

        if html_text:
            part2 = MIMEText(html_text, 'html')
            outer.attach(part2)

        if not attachments:
            attachments = []

        for path in attachments:
            content_type, encoding = mimetypes.guess_type(path)

            if content_type is None or encoding is not None:
                # No guess could be made, or the file is encoded (compressed), so
                # use a generic bag-of-bits type.
                content_type = 'application/octet-stream'

            maintype, subtype = content_type.split('/', 1)
            if maintype == 'text':
                with codecs.open(path, 'r', 'utf-8') as file_handle:
                    # Note: we should handle calculating the charset
                    msg = MIMEText(file_handle.read(), _subtype=subtype)
            elif maintype == 'image':
                with open(path, 'rb') as file_handle:
                    msg = MIMEImage(file_handle.read(), _subtype=subtype)
            elif maintype == 'audio':
                with open(path, 'rb') as file_handle:
                    msg = MIMEAudio(file_handle.read(), _subtype=subtype)
            else:
                with open(path, 'rb') as file_handle:
                    msg = MIMEBase(maintype, subtype)
                    msg.set_payload(file_handle.read())
                # Encode the payload using Base64
                encoders.encode_base64(msg)
            # Set the filename parameter
            msg.add_header('Content-Disposition', 'attachment', filename=path)
            outer.attach(msg)

        for i in range(try_num):
            try:
                if i != 0:
                    time.sleep(2)
                if self.use_ssl:
                    smtp = smtplib.SMTP_SSL(self.server)
                else:
                    smtp = smtplib.SMTP()
                    smtp.connect(self.server)
                smtp.login(self.user, self.password)
                dst_list = str_to.split(",")
                if len(dst_list) <= 8:
                    smtp.sendmail(self.str_from, dst_list, outer.as_string())
                else:
                    smtp.sendmail(self.str_from, dst_list[0:8], outer.as_string())
                    smtp.sendmail(self.str_from, dst_list[8:], outer.as_string())
                smtp.quit()
                return True
            except Exception as e:
                log.warning(str(e))
        return False
