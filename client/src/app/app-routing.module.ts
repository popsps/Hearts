import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginGuard} from "./shared/guards/login.guard";

const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'home'},
  {path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule), canActivate: [LoginGuard]},
  {path: 'play', loadChildren: () => import('./play/play.module').then(m => m.PlayModule), canActivate: [LoginGuard]},
  {
    path: 'scoreboard',
    loadChildren: () => import('./scoreboard/scoreboard.module').then(m => m.ScoreboardModule),
    canActivate: [LoginGuard]
  },
  {path: 'home', loadChildren: () => import('./home/home.module').then(m => m.HomeModule), canActivate: [LoginGuard]},
  {
    path: 'history',
    loadChildren: () => import('./history/history.module').then(m => m.HistoryModule),
    canActivate: [LoginGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
