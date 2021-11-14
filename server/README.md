# Hearts Server

## Installation and Setup

...

## API Endpoints

### Registering a new user

Request POST https://localhost:8081/api/auth/register

```json
{
  "username": "winniepooh",
  "password": "Password1!",
  "email": "winniepooh@disnep.com",
  "nickname": "Winnie the pooh"
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

Request GET https://localhost:8081/api/games/current/play/heartbeat

### Play a card

Request POST https://localhost:8081/api/games/current/play/card

```json
{
  "suit": "SPADES",
  "rank": "ACE"
}
```


