import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CardComponent} from "./components/card/card.component";
import {CardSetComponent} from './components/card-set/card-set.component';
import {DragDropModule} from "@angular/cdk/drag-drop";
import {MatRadioModule} from "@angular/material/radio";
import {LoadingSpinnerComponent} from "./components/loading-spinner/loading-spinner.component";
import {NavigationComponent} from "./components/navigation/navigation.component";
import {ErrorDialogComponent} from "./components/error-dialog/error-dialog.component";
import {ButtonComponent} from "./components/button/button.component";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatToolbarModule} from "@angular/material/toolbar";
import {AppRoutingModule} from "../app-routing.module";
import {RouterModule} from "@angular/router";
import { PlayerStatusComponent } from './components/player-status/player-status.component';
import { TimerComponent } from './components/timer/timer.component';
import {MatCardModule} from "@angular/material/card";
import { PlayerInfoComponent } from './components/player-info/player-info.component';
import {MatBadgeModule} from "@angular/material/badge";
import { GameResultModalComponent } from './components/game-result-modal/game-result-modal.component';
import {MatDialogModule} from "@angular/material/dialog";

@NgModule({
  declarations: [
    NavigationComponent,
    LoadingSpinnerComponent,
    ErrorDialogComponent,
    ButtonComponent,
    CardComponent,
    CardSetComponent,
    LoadingSpinnerComponent,
    PlayerStatusComponent,
    TimerComponent,
    PlayerInfoComponent,
    GameResultModalComponent
  ],
  exports: [
    LoadingSpinnerComponent,
    CardComponent,
    CardSetComponent,
    ErrorDialogComponent,
    NavigationComponent,
    PlayerStatusComponent,
    TimerComponent,
    PlayerInfoComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    DragDropModule,
    MatRadioModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatCardModule,
    MatBadgeModule,
    MatDialogModule
  ]
})
export class SharedModule {
}
