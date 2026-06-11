package main

import (
	"math/rand"
)

func Referee(p1 Organization, p2 Organization, result chan Organization) {
	chooser, other := p1, p2
	if rand.Intn(2) == 1 {
		chooser, other = p2, p1
	}

	chooser.CommunicationChannel <- Msg{Type: Choice}
	guess := <-chooser.PlayerMoveChannel

	chooser.CommunicationChannel <- Msg{Type: Next}
	other.CommunicationChannel <- Msg{Type: Next}
	sum := (<-chooser.PlayerMoveChannel).Value + (<-other.PlayerMoveChannel).Value

	loser, winner := other, chooser
	if sum%2 != guess.Value {
		loser, winner = chooser, other
	}

	loser.CommunicationChannel <- Msg{Type: Lose}
	result <- winner
}
