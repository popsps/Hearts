import {Component, OnDestroy, OnInit} from '@angular/core';
import {PlayService} from "../shared/services/play.service";
import {Card} from "../shared/models/card";
import {UserService} from "../shared/services/user.service";
import {Player} from "../shared/models/player";
import {Router} from "@angular/router";
import {Subscription, takeWhile} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {GameResultModalComponent} from "../shared/components/game-result-modal/game-result-modal.component";
import {Opponent} from "../shared/models/opponent";
import {Play} from "../shared/models/play";

@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit, OnDestroy {
  cards!: Card[];
  opponents: Opponent[];
  username: string;
  you: Player;
  timer: number;
  gameStatus: string;
  subscription: Subscription;

  constructor(public playService: PlayService,
              public userService: UserService,
              private router: Router,
              private matDialog: MatDialog) {
  }

  ngOnDestroy(): void {
    console.log('housekeeping...');
    if (this.subscription) {
      console.log('remove dangling subscriptions...');
      this.subscription.unsubscribe();
    }
    this.playService.gameManager = null;
  }

  ngOnInit(): void {
    this.subscription =
      this.playService.heartbeatInterval
        .pipe(takeWhile(_ => {
          if (this.playService.gameManager?.status === 'ENDED') {
            this.openTableResult();
            // unsubscribe from heartbeat
            console.log('housekeeping in heartbeat take while...');
            return false;
          } else
            return true;
        }))
        .subscribe(_ => {
          this.playService.heartbeat()
            .subscribe({
              next: res => {
                this.playService.setPlay(res);
                if (this.playService.wasPassTheTrash && !this.playService.gameManager.passTheTrash)
                  this.playService.setCardsAfterPassTheTrash(res.you.cards);
                else if (this.playService.gameManager.cardsRemaining === 52)
                  this.playService.setCardsAfterPassTheTrash(res.you.cards);
                else
                  this.playService.setCards(res.you.cards);
                this.playService.wasPassTheTrash = this.playService.gameManager.passTheTrash;
                this.cards = this.playService.getCards();
                this.opponents = this.playService.getOpponents();
                this.playService.board = res.board;
                this.you = this.playService.gameManager.you;
                this.timer = this.playService.gameManager.timer;
                this.gameStatus = this.playService.gameManager.status;
                this.playService.boardSize = Object.keys(this.playService.board).length;
                // if (this.playService.gameManager.status === 'ENDED') {
                //   this.openTableResult();
                //   // unsubscribe from heartbeat
                //   console.log('housekeeping in heartbeat...');
                //   if (this.subscription) {
                //     this.subscription.unsubscribe();
                //   }
                // }
              },
              error: err => {
                this.router.navigate(['/home']).then();
              }
            });
        });
  }

  playCard() {
    if (!this.playService.gameManager.passTheTrash) {
      this.playService.playCard().subscribe(res => {
        this.setGameManager(res);
      });
    } else {
      this.playService.passTrash().subscribe(res => {
        this.setGameManager(res, true);
      });
    }
  }

  setGameManager(res: Play, reload = false) {
    this.playService.setPlay(res);
    if (reload)
      this.playService.setCardsAfterPassTheTrash(res.you.cards);
    else
      this.playService.setCards(res.you.cards);
    this.cards = this.playService.getCards();
    this.opponents = this.playService.getOpponents();
    this.playService.board = res.board;
    this.you = this.playService.gameManager.you;
    this.timer = this.playService.gameManager.timer;
    this.gameStatus = this.playService.gameManager.status;
    this.playService.boardSize = Object.keys(this.playService.board).length;
  }

  openTableResult() {
    const dialogRef = this.matDialog.open(GameResultModalComponent, {
      data: {players: this.playService.gameManager.resultTable, status: this.playService.gameManager.status},
      width: '400px',
    });
    dialogRef.afterClosed().subscribe(value => {
      if (this.playService.gameManager.status === 'ENDED') {
        this.router.navigate(['/home']).then();
      }
    });
  }
}
