import {User} from "./user";

export class Game {
  id: number;
  status: string;
  logs: string[];
  sessionCreated: string;
  sessionEnded: string;
  users: User[];
}
