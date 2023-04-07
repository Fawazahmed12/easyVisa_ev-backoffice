export class ReplyCheck {
  id: number = null;
  reply: string = null;

  constructor(obj) {
    for (const key in obj) {
      if (typeof this[key] !== 'undefined') {
        this[key] = obj[key];
      }
    }
  }
}
