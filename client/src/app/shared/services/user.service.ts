import {Injectable} from '@angular/core';
import {User} from "../models/user";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {LoginDto} from "../models/login-dto";
import {Game} from "../models/game";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  isLoggedIn = false;
  isLoggedInChecked = false;
  loading = false;
  error = false;
  errorMessage: string = '';
  user: User | undefined;
  profilePicture: any;

  constructor(private httpClient: HttpClient) {
  }

  public login(loginDto: LoginDto): Observable<User> {
    return this.httpClient.post<User>(`api/auth/authenticate`, loginDto);
  }

  public logout(): Observable<any> {
    return this.httpClient.delete(`api/auth/logout`);
  }

  public getUserInfo(): Observable<User> {
    return this.httpClient.get<User>('/api/users/user-info');
  }

  public getProfilePicture(username: string = null): Observable<any> {
    const option: Object = {responseType: 'blob'};
    const url: string = (username) ? `/api/users/profile-pic/${username}` : '/api/users/profile-pic';
    return this.httpClient.get<any>(url, option);
  }

  public updateProfilePicture(formData: FormData): Observable<any> {
    return this.httpClient
      .post<any>('/api/users/profile-pic', formData);
  }


  public getScoreboard(): Observable<User[]> {
    return this.httpClient.get<User[]>('api/users');
  }

  public getGamesHistory(): Observable<Game[]> {
    return this.httpClient.get<Game[]>('api/games');
  }

  getSession() {
    return this.httpClient.get<User>('api/auth/session');
  }

  signup(newUser: User) {
    return this.httpClient.post<User>('api/auth/register', newUser);
  }

  setUser(user: User) {
    this.isLoggedIn = true;
    this.user = user;
    this.loading = false;
    this.error = false;
  }


  clearUser() {
    this.isLoggedIn = false;
    this.user = null;
    this.loading = false;
  }
}
