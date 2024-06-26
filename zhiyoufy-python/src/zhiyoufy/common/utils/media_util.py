import contextlib
import wave
import random
import hashlib


class MediaUtil:
    @staticmethod
    def read_wave(wave_path, add_noise=True):
        with wave.open(wave_path, "rb") as wave_file:
            speech = wave_file.readframes(wave_file.getnframes())
            if add_noise:
                speech = bytearray(speech)
                for idx in range(0, len(speech), 2):
                    choice = random.randint(1, 2)
                    if choice == 1:
                        if speech[idx] & 0x01:
                            speech[idx] &= 0xFE
                        else:
                            speech[idx] |= 0x01
                    else:
                        if speech[idx] & 0x02:
                            speech[idx] &= 0xFD
                        else:
                            speech[idx] |= 0x02
                speech = bytes(speech)
            m = hashlib.sha1()
            m.update(speech)
            digest = m.hexdigest()
            return speech, digest
    
    @staticmethod
    def pcm2wav(pcm_file, wav_file, channels=1, bits=16, sample_rate=16000):
        with open(pcm_file, 'rb') as pcm_file:
            pcm_data = pcm_file.read()

        if bits % 8 != 0:
            raise ValueError("bits % 8 must == 0. now bits:" + str(bits))

        with wave.open(wav_file, 'wb') as wav_file:
            wav_file.setnchannels(channels)
            wav_file.setsampwidth(bits // 8)
            wav_file.setframerate(sample_rate)
            wav_file.writeframes(pcm_data)

    @staticmethod
    def get_wav_duration_ms(wav_file):
        with contextlib.closing(wave.open(wav_file, 'r')) as f:
            frames = f.getnframes()
            rate = f.getframerate()
            wav_duration = frames/float(rate)
            return wav_duration*1000
