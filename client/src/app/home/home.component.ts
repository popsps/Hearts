import {Component, OnDestroy, OnInit} from '@angular/core';
import {PlayService} from "../shared/services/play.service";
import {Router} from "@angular/router";
import {Subscription} from "rxjs";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

  subscriptions: Subscription[] = [];
  profilePicture: any;

  constructor(public userService: UserService, public playService: PlayService, private router: Router) {
  }

  ngOnInit(): void {
    this.userService.getUserInfo().subscribe({
      next: value => {
        this.userService.user = value;
        this.getProfilePicture();
      },
      error: err => {
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

//   res => {
//   const subscription = this.playService.statusInterval.subscribe(_ => {
//     this.playService.getStatus().subscribe(res => {
//       this.playService.lookingForGame = false;
//       this.router.navigate(['/play']).then();
//     }, error => {
//       this.playService.lookingForGame = false;
//     });
//   });
// }, error => {
//   this.playService.lookingForGame = false;
//   this.router.navigate(['/play']).then();
// }
  join() {
    this.playService.lookingForGame = true;
    if (this.playService.lookingForGame) {
      this.playService.join().subscribe({
        next: userStatus => {
          if (userStatus.inGame) {
            this.playService.lookingForGame = false;
            this.router.navigate(['/play']).then();
          } else {
            const sb = this.playService.statusInterval.subscribe({
              next: _ => {
                this.playService.getStatus().subscribe({
                  next: userStatus => {
                    if (userStatus.inGame) {
                      this.playService.lookingForGame = false;
                      this.router.navigate(['/play']).then();
                    }
                    if (!userStatus.inJoiningPool) {
                      this.playService.lookingForGame = false;
                      sb.unsubscribe();
                    }
                  },
                  error: err => {
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

        }
      });
    }
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
