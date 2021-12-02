import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PlayComponent} from './play.component';

const routes: Routes = [
  {
    path: '', component: PlayComponent,
    children: [
      {path: 'login', component: PlayComponent},
      {path: 'signup', component: PlayComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlayRoutingModule {
}
