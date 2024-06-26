export class RandomUtils {
  static numberSeq(length: number): string {
    let seq = '';

    for (let i = 0; i < length; i++) {
      seq += Math.floor(Math.random() * 10);
    }

    return seq;
  }
}
