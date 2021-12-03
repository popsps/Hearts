import {Opponent} from "./opponent";
import {Player} from "./player";
import {Board} from "./board";

export class Play {
  id: number;
  heartsBroken: boolean;
  passTheTrash: boolean;
  cardsRemaining: number;
  timer: number;
  opponents: Opponent[];
  board: Board;
  you: Player;
  status: string;
  sessionCreated: string;
  resultTable: Opponent[];
}
