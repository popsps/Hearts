import {Component, Inject, OnInit} from '@angular/core';
import {Opponent} from "../../models/opponent";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {PlayService} from "../../services/play.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-game-result-modal',
  templateUrl: './game-result-modal.component.html',
  styleUrls: ['./game-result-modal.component.scss']
})
export class GameResultModalComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: { players: Opponent[], status: string },
              private playService: PlayService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  leaveGame() {
    this.playService.disconnect().subscribe(res => {
      if (!res.inGame) {
        this.router.navigate(['/']).then();
      }
    });
  }
}
