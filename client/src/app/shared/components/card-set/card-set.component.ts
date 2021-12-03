import {Component, Input, OnInit} from '@angular/core';
import {PlayService} from "../../services/play.service";
import {Card} from "../../models/card";

const cardStyles = {
  'card-left': {
    'transform': 'translate(50px, 0px) rotate(-20deg)',
  },
  'card-mid': {},
  'card-right': {
    'transform': 'translate(-50px, 0px) rotate(20deg)',
  }
};

@Component({
  selector: 'app-card-set',
  templateUrl: './card-set.component.html',
  styleUrls: ['./card-set.component.scss']
})
export class CardSetComponent implements OnInit {

  @Input()
  set cards(value: Card[]) {
    this._cards = value;
    this.computeStyles();
  }

  _cards?: Card[];

  @Input()
  set count(value: number) {
    if (this.back && value) {
      const backCard = new Card();
      backCard.back = true;
      this._count = value;
      this._cards = new Array(this._count).fill(backCard);
      this.computeStyles();
    }
  }

  @Input()
  interactive: boolean = false;

  _count?: number;
  @Input() back = false;

  cardStyles: any[]

  constructor(public playService: PlayService) {
  }

  ngOnInit(): void {
  }

  computeStyles() {
    // const midCard = (this._cards.length % 2 === 1) ? Math.ceil(this._cards.length / 2) : Math.floor(this._cards.length / 2);
    const midCard = Math.floor(this._cards.length / 2);
    this.cardStyles = this._cards.map((card, i) => {
      if (i < midCard) {
        const j = midCard - i;
        // return {'transform': `translate(${j * 60}px, ${j * 1.1}px) rotate(${-8 * j}deg)`, 'z-index': `5`};
        // return {'transform': `translate(${j * 70}px, ${0}px)  rotate(${-10 * j}deg)`, 'z-index': `5`};
        // return {'transform': `translate(${j * 80}px, ${j * 20}px)  rotate(${-10 * j}deg)`, 'z-index': `5`};
        if (this.back)
          return {'transform': `translate(${j * 90}px, 0px)`, 'z-index': `5`};
        else
          return {'transform': `translate(${j * 40}px, 0px)`, 'z-index': `5`};
        // return { 'z-index': `5`};
      } else if (i === midCard)
        // return {'transform': `translate(${0}px, ${15}px) `, 'z-index': `5`};
        return {'z-index': `5`};
      else {
        const j = i - midCard;
        // return {'transform': `translate(${-j * 60}px, ${j * 1.1}px) rotate(${8 * j}deg)`, 'z-index': `5`};
        // return {'transform': `translate(${-j * 70}px, ${0}px) rotate(${10 * j}deg)`, 'z-index': `5`};
        // return {'transform': `translate(${-j * 80}px, ${j * 20}px) rotate(${10 * j}deg)`, 'z-index': `5`};
        if (this.back)
          return {'transform': `translate(${-j * 90}px, 0px)`, 'z-index': `5`};
        else
          return {'transform': `translate(${-j * 40}px, 0px)`, 'z-index': `5`};
        // return { 'z-index': `5`};
      }
    });
  }
}
