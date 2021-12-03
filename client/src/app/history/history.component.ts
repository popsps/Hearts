import {Component, OnInit} from '@angular/core';
import {UserService} from "../shared/services/user.service";
import {Game} from "../shared/models/game";

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss']
})
export class HistoryComponent implements OnInit {

  games: Game[];
  isError: boolean = false;
  isLoading: boolean = false;

  constructor(public userService: UserService) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.userService.getGamesHistory().subscribe({
      next: value => {
        this.games = value;
        this.isLoading = false;
        this.isError = false;
        this.userService.error = false;
      },
      error: err => {
        this.userService.loading = false;
        this.isError = true;
        this.userService.error = true;
        this.isLoading = false;
      }
    });
  }

}
