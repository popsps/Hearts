import {Component, OnInit} from '@angular/core';
import {UserService} from "../shared/services/user.service";
import {User} from "../shared/models/user";

@Component({
  selector: 'app-scoreboard',
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.scss']
})
export class ScoreboardComponent implements OnInit {

  users: User[];
  displayedColumns: string[] = ['username', 'nickname', 'win', 'lost', 'averagePlacement', 'pointsTakenPerGame'];
  isError: boolean = false;
  isLoading: boolean = false;

  constructor(public userService: UserService) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.userService.getScoreboard().subscribe({
      next: value => {
        this.users = value;
        this.isLoading = false;
        // this.userService.loading = false;
        // this.isError = false;
        // this.userService.error = false;
        // this.userService.errorMessage = 'Sorry, We cannot process your request at this time';
      },
      error: err => {
        this.isLoading = false;
        // this.userService.loading = false;
        // this.isError = true;
        // this.userService.error = true;
        // this.userService.errorMessage = 'Sorry, We cannot process your request at this time';
      }
    });
  }

}
