import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HistoryRoutingModule } from './history-routing.module';
import { HistoryComponent } from './history.component';
import {MatExpansionModule} from "@angular/material/expansion";
import {SharedModule} from "../shared/shared.module";


@NgModule({
  declarations: [
    HistoryComponent
  ],
  imports: [
    CommonModule,
    HistoryRoutingModule,
    MatExpansionModule,
    SharedModule
  ]
})
export class HistoryModule { }
