import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-player-status',
  templateUrl: './player-status.component.html',
  styleUrls: ['./player-status.component.scss']
})
export class PlayerStatusComponent implements OnInit {

  @Input()
  turn: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

}
