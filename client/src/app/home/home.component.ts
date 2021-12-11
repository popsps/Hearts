import {Component, OnDestroy, OnInit} from '@angular/core';
import {PlayService} from "../shared/services/play.service";
import {Router} from "@angular/router";
import {Subscription, takeWhile} from "rxjs";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

  subscriptions: Subscription[] = [];
  profilePicture: any;
  userLoading = false;
  userError = false;

  constructor(public userService: UserService, public playService: PlayService, private router: Router) {
  }

  ngOnInit(): void {
    this.userLoading = true;
    this.userService.getUserInfo().subscribe({
      next: value => {
        this.userService.user = value;
        this.getProfilePicture();
        this.getPlayerStatus();
        this.userLoading = false;
        this.userError = false;
      },
      error: err => {
        this.userLoading = false;
        this.userError = true;
      }
    });

  }

  getProfilePicture() {
    this.userService.getProfilePicture().subscribe({
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
      }
    });
  }

  join() {
    this.playService.lookingForGame = true;
    this.playService.join().subscribe({
      next: userStatus => {
        if (userStatus.inGame) {
          this.playService.lookingForGame = false;
          this.playService.inGame = true;
          this.router.navigate(['/play']).then();
        } else {
          const sb = this.playService.statusInterval.subscribe({
            next: _ => {
              this.playService.getStatus().subscribe({
                next: userStatus => {
                  this.playService.lookingForGame = userStatus.inJoiningPool;
                  this.playService.inGame = userStatus.inGame;
                  if (userStatus.inGame) {
                    this.playService.lookingForGame = false;
                    this.playService.inGame = true;
                    this.router.navigate(['/play']).then();
                  }
                  if (!userStatus.inJoiningPool) {
                    this.playService.lookingForGame = false;
                    sb.unsubscribe();
                  }
                },
                error: err => {
                  this.playService.lookingForGame = false;
                  this.playService.inGame = false;
                }
              })
            },
            error: err => {
            }
          });
          this.subscriptions.push(sb);
        }
      },
      error: err => {
        this.playService.lookingForGame = false;
        this.playService.inGame = false;
      }
    });
  }

  getPlayerStatus(): void {
    this.playService
      .getStatus()
      .subscribe({
        next: userStatus => {
          this.playService.lookingForGame = userStatus.inJoiningPool;
          this.playService.inGame = userStatus.inGame;
          if (this.playService.lookingForGame)
            this.join();
        },
        error: err => {
        }
      });
  }

  disconnect(): void {
    this.playService.disconnect().subscribe(res => {
      this.playService.lookingForGame = res.inJoiningPool;
      this.playService.inGame = res.inGame;
    });
  }

  ngOnDestroy(): void {
    if (this.subscriptions) {
      this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }
  }

  updateProfilePicture(event: any) {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      const file: File = files[0];
      const formData = new FormData();
      formData.append('file', file);
      this.userService.updateProfilePicture(formData).subscribe(value => {
        this.getProfilePicture();
      });
    }

  }
}
