import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {UserService} from "../services/user.service";

@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.userService.isLoggedIn) {
      if (state.url === '/auth/signup' || state.url === '/auth/login')
        this.router.navigate(['home']).then();
    } else if (this.userService.isLoggedInChecked && !this.userService.isLoggedIn) {
      if (state.url !== '/auth/login' && state.url !== '/auth/signup') {
        this.router.navigate(['auth/login']).then();
      }
    } else if (!this.userService.isLoggedInChecked) {
      this.userService.loading = true;
      this.userService.getSession().subscribe({
        next: user => {
          this.userService.user = user;
          this.userService.isLoggedIn = true;
          this.userService.isLoggedInChecked = true;
          this.userService.loading = false;
          if (state.url === '/auth/signup' || state.url === '/auth/login')
            this.router.navigate(['home']).then();
        },
        error: err => {
          this.userService.loading = false;
          this.userService.isLoggedInChecked = true;
          this.router.navigate(['auth/login']).then();
        }
      });
    }
    return true;
  }

}
