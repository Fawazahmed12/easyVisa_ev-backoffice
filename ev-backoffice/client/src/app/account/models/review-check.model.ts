export class ReviewCheck {
  id: number = null;
  rating: number = null;
  title: string = null;
  review: string = null;

  constructor(obj) {
    for (const key in obj) {
      if (typeof this[key] !== 'undefined') {
        this[key] = obj[key];
      }
    }
  }
}
