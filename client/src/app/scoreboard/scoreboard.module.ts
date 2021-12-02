import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ScoreboardRoutingModule } from './scoreboard-routing.module';
import { ScoreboardComponent } from './scoreboard.component';
import {MatTableModule} from "@angular/material/table";
import {SharedModule} from "../shared/shared.module";


@NgModule({
  declarations: [
    ScoreboardComponent
  ],
  imports: [
    CommonModule,
    ScoreboardRoutingModule,
    MatTableModule,
    SharedModule,
  ]
})
export class ScoreboardModule { }
