import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PlayRoutingModule } from './play-routing.module';
import { PlayComponent } from './play.component';
import { HandComponent } from './hand/hand.component';
import {SharedModule} from "../shared/shared.module";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";


@NgModule({
  declarations: [
    PlayComponent,
    HandComponent
  ],
  imports: [
    CommonModule,
    PlayRoutingModule,
    SharedModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class PlayModule { }
