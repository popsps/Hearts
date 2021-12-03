import {Component, Input, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-player-info',
  templateUrl: './player-info.component.html',
  styleUrls: ['./player-info.component.scss']
})
export class PlayerInfoComponent implements OnInit {
  @Input()
  name: string;
  @Input()
  username: string;
  @Input()
  pointsTaken: number;
  @Input()
  pointsTakenOverall: number;
  @Input()
  turn: boolean;

  profilePicture: any;

  constructor(public userService: UserService) {
  }

  ngOnInit(): void {
    console.log('run info ...');
    this.userService.getProfilePicture(this.username).subscribe({
      next: (image: Blob) => {
        if (image && image.size > 0) {
          const reader = new FileReader();
          reader.addEventListener('load', ev => {
            this.profilePicture = reader.result;
          }, false);
          reader.readAsDataURL(image);
        }
      },
      error: err => {
        this.userService.loading = false;
        this.userService.error = true;
      }
    });
  }

}
