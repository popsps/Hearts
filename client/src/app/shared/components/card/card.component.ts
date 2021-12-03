import {Component, Input, OnInit} from '@angular/core';
import {Card} from "../../models/card";
import {PlayService} from "../../services/play.service";

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {


  @Input()
  set card(c: Card) {
    if (c) {
      if (c.back)
        this.type = 'BACK';
      else
        this.type = `${c.rank}_${c.suit}`;
      this.myCard = c;
    }
  }

  @Input()
  interactive: boolean = false;

  myCard!: Card;
  type!: string;
  selected: boolean = false;


  constructor(private playService: PlayService) {
  }

  ngOnInit(): void {
  }

  selectCard() {
    if (this.playService.gameManager.you.turn) {
      if (!this.playService.gameManager.passTheTrash) {
        this.playService.selectCard(this.myCard);
      } else {
        this.playService.selectTrash(this.myCard);
      }
    }
  }

  cardIsAllowed() {
    if (this.interactive)
      return !!this.playService
        .player
        .allowedCards
        .find(c => c.suit === this.myCard.suit && c.rank === this.myCard.rank);
    else
      return true;
  }
}
