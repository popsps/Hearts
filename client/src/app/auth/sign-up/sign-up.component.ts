import {Component, OnInit} from '@angular/core';
import {User} from "../../shared/models/user";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {UserService} from "../../shared/services/user.service";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

  newUser: User;
  signupForm: FormGroup;

  constructor(private router: Router, private userService: UserService, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm() {
    this.signupForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      email: ['', [Validators.required]],
      nickname: ['', [Validators.required]],
      firstname: [''],
      lastname: [''],
    });
    this.signupForm.valueChanges.subscribe(value => {
      this.newUser = new User();
      this.newUser.username = value.username;
      this.newUser.password = value.password;
      this.newUser.email = value.email;
      this.newUser.nickname = value.nickname;
      this.newUser.firstname = value.firstname;
      this.newUser.lastname = value.lastname;
    });

  }

  signup(): void {
    this.userService.error = false;
    this.userService.signup(this.newUser).subscribe({
      next: user => {
        const {username, password} = this.newUser;
        this.userService.login({username, password}).subscribe({
          next: value => {
            this.userService.setUser(user);
            this.router.navigate(['/']).then();
          },
          error: err => {
            this.userService.error = true;
            this.userService.errorMessage = 'Something went wrong when logging you in.';
          }
        });
      },
      error: err => {
        this.userService.error = true;
        this.userService.errorMessage = 'User is not available or bad input provided';
      }
    });
  }

}
