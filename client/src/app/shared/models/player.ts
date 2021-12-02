import {Card} from "./card";

export class Player {
  allowedCards: Card[];
  cards: Card[];
  id: number;
  username: string;
  nickname: string;
  turnExpireAt: string;
  disconnected: boolean;
  turn: boolean;
  lastTrickTaken: boolean;
  numberOfRemainingCards: number;
  pointsTaken: number;
  pointsTakenOverall: number;
  placement: number;
}
