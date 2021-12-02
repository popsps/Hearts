import {Card} from "./card";

class Player {
  card: Card;
  turn: boolean;
}

export class Board {
  [key: string]: Card;
}
