import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

  constructor(public userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
  }

  logout() {
    this.userService.logout().subscribe(value => {
      console.log('logout successful');
      this.userService.clearUser();
      this.router.navigate(['/auth/login']).then();
    });
  }
}
