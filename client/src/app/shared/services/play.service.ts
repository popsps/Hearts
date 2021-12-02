import {Injectable} from '@angular/core';
import {Play} from "../models/play";
import {HttpClient} from "@angular/common/http";
import {interval, Observable} from "rxjs";
import {Card} from "../models/card";
import {Player} from "../models/player";
import {Board} from "../models/board";
import {UserStatus} from "../models/user-status";

@Injectable({
  providedIn: 'root'
})
export class PlayService {
  loading = false;
  error = false;
  errorMessage: string = '';
  gameManager!: Play;
  player!: Player;
  board: Board;
  cards: Card[] = [];
  card: Card = null;
  trash: Card[] = [];
  selected: boolean = false;
  heartbeatInterval: Observable<number>;
  statusInterval: Observable<number>;
  status: string;
  lookingForGame: boolean = false;
  boardSize: number;
  wasPassTheTrash: boolean;

  constructor(private httpClient: HttpClient) {
    this.heartbeatInterval = interval(1000);
    this.statusInterval = interval(1000);
  }

  join(): Observable<UserStatus> {
    const requestOptions: Object = {
      /* other options here */
      responseType: 'text'
    }
    return this.httpClient.post<UserStatus>('/api/games/join', null);
  }

  disconnect(): Observable<UserStatus> {
    return this.httpClient.post<UserStatus>('/api/games/disconnect', null);
  }

  getStatus(): Observable<UserStatus> {
    return this.httpClient.get<UserStatus>('/api/play/status');
  }

  heartbeat(): Observable<Play> {
    return this.httpClient.get<Play>('/api/play/heartbeat');
  }

  playCard(): Observable<Play> {
    return this.httpClient.post<Play>('/api/play/card', this.card);
  }

  setPlay(play: Play) {
    this.loading = false;
    this.gameManager = play;
    this.player = this.gameManager.you;
  }

  getCards() {
    return this.cards;
  }

  setCards(cards: Card[]) {
    // remove cards if they do not exist in new version
    for (let i = 0; i < this.cards.length; i++) {
      const oldCard: Card = this.cards[i];
      const newCard = cards?.find(c => c.suit === oldCard.suit && c.rank === oldCard.rank);
      if (!newCard) {
        this.cards = this.cards.filter(c => c.suit !== oldCard.suit && c.rank !== oldCard.rank);
      }
    }
    for (let i = 0; i < cards.length; i++) {
      const card: Card = cards[i];
      const existingCard = this.cards?.find(c => c.suit === card.suit && c.rank === card.rank);
      if (!existingCard) {
        this.cards.splice(i, 0, card);
      }
    }
  }

  setCardsAfterPassTheTrash(cards: Card[]) {
    const _cards: Card[] = cards.map(card => {
      const existingCard = this.cards?.find(c => c.suit === card.suit && c.rank === card.rank);
      if (existingCard) {
        card.selected = existingCard.selected;
        card.back = existingCard.back;
      }
      return card;
    });
    this.cards = _cards;
  }

  getOpponents() {
    return this.gameManager.opponents;
  }

  passTrash(): Observable<Play> {
    const myTrash: Card[] = this.trash.map(card => {
      const trashCard = new Card();
      trashCard.suit = card.suit;
      trashCard.rank = card.rank;
      return trashCard;
    });
    console.log('here is my trash', myTrash);
    return this.httpClient.post<Play>('/api/play/pass-trash', myTrash);
  }

  selectTrash(card: Card) {
    if (this.player.allowedCards.find(c => c.suit === card.suit && c.rank === card.rank)) {
      card.selected = !card.selected;
      if (card.selected) {
        this.trash = [...this.trash, card];
        if (this.trash.length > 3) {
          const removedCard = this.trash.shift();
          this.cards
            .find(card => card.suit === removedCard.suit && card.rank === removedCard.rank)
            .selected = false;
        }
      } else {
        this.trash = this.trash.filter(c => c.suit !== card.suit || c.rank !== card.rank);
      }
    }
  }


  selectCard(card: Card) {
    // this.cards.forEach(c => c.selected = false);
    if (this.player.allowedCards.find(c => c.suit === card.suit && c.rank === card.rank)) {
      card.selected = !card.selected;
      if (card.selected) {
        if (this.card && this.cards) {
          const oldCard: Card = this.cards.find(c => c?.selected === true && (c.suit !== card.suit || c.rank !== card.rank));
          if (oldCard)
            oldCard.selected = false;
        }
        this.card = new Card();
        this.card.suit = card.suit;
        this.card.rank = card.rank;

      } else {
        this.card = null;
      }
    }
  }
}
