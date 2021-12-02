import {Stat} from "./stat";

export class User {
  id?: number;
  username?: string;
  password?: string;
  nickname?: string;
  firstname?: string;
  lastname?: string;
  phone?: string;
  email?: string;
  stats?: Stat;
}
