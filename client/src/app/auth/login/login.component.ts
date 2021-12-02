import {Component, OnInit} from '@angular/core';
import {UserService} from "../../shared/services/user.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validator, Validators} from "@angular/forms";
import {LoginDto} from "../../shared/models/login-dto";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginDto!: LoginDto;
  loginForm!: FormGroup;

  constructor(private userService: UserService, private router: Router,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  login(): void {
    this.userService.error = false;
    this.userService.login(this.loginDto).subscribe({
      next: res => {
        this.userService.setUser(res);
        this.router.navigate(['/']).then();
      },
      error: err => {
        this.userService.error = true;
        this.userService.errorMessage = "Invalid credentials";
      }
    });
  }

  private initForm() {
    this.loginForm = this.fb.group({
      id: ['', [Validators.required]],
      password: ['', [Validators.required]],
    });
    this.loginForm.valueChanges.subscribe(value => {
      this.loginDto = new LoginDto();
      this.loginDto.username = value.id;
      this.loginDto.password = value.password;
    });
  }
}
