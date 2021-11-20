# Hearts Server

## Installation and Setup

...

## API Endpoints

The content of these endpoints is subject to change

### Registering a new user

Request POST https://localhost:8081/api/auth/register

```json
{
  "username": "winniepooh",
  "password": "Password1!",
  "email": "winniepooh@disnep.com",
  "nickname": "Winnie the pooh",
  "firstname": "Winnie",
  "lastname": "Pooh"
}
```

### Login a user

Request POST https://localhost:8081/api/auth/authenticate

```json
{
  "username": "winniepooh",
  "password": "Password1!"
}
```

### Join a game

Request POST https://localhost:8081/api/games/join

### Send periodical heartbeats after joining a game

Request GET https://localhost:8081/api/play/heartbeat

Example Response body

```json
{
  "id": 0,
  "leadingSuit": "CLUBS",
  "heartBroken": false,
  "cardsRemaining": 15,
  "opponents": [
    {
      "username": "tigger",
      "nickname": "Tigger",
      "numberOfRemainingCards": 8,
      "lastTrickTaken": false,
      "turn": false,
      "placement": 0,
      "pointsTaken": 0,
      "pointsTakenOverall": 0
    }
  ],
  "board": {
    "tigger": {
      "suit": "CLUBS",
      "rank": "TWO"
    }
  },
  "you": {
    "id": 3,
    "username": "winniepooh",
    "nickname": "Winnie the pooh",
    "turnExpireAt": "2021-11-14T18:43:45.5214429",
    "turn": true,
    "lastTrickTaken": false,
    "numberOfRemainingCards": 8,
    "allowedCards": [
      {
        "suit": "CLUBS",
        "rank": "ACE"
      },
      {
        "suit": "CLUBS",
        "rank": "QUEEN"
      }
    ],
    "cards": [
      {
        "suit": "CLUBS",
        "rank": "ACE"
      },
      {
        "suit": "SPADES",
        "rank": "QUEEN"
      },
      {
        "suit": "DIAMONDS",
        "rank": "JACK"
      },
      {
        "suit": "HEARTS",
        "rank": "QUEEN"
      },
      {
        "suit": "CLUBS",
        "rank": "QUEEN"
      },
      {
        "suit": "DIAMONDS",
        "rank": "ACE"
      },
      {
        "suit": "HEARTS",
        "rank": "ACE"
      },
      {
        "suit": "SPADES",
        "rank": "KING"
      }
    ],
    "pointsTaken": 0,
    "pointsTakenOverall": 0,
    "placement": 0
  },
  "status": "IN_PROGRESS",
  "sessionCreated": "2021-11-14T17:53:30.3032122"
}
```

### Play a card

Request POST https://localhost:8081/api/play/card

```json
{
  "suit": "SPADES",
  "rank": "ACE"
}
```


