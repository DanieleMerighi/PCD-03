package actors

import (
	"math/rand"
	"oddsAndEvens/internal/model"
)

func Referee(p1 Organization, p2 Organization, result chan Organization) {
	chooser, other := p1, p2
	if rand.Intn(2) == 1 {
		chooser, other = p2, p1
	}

	chooser.CommunicationChannel <- model.Msg{Type: model.Choice}
	guess := <-chooser.PlayerMoveChannel

	chooser.CommunicationChannel <- model.Msg{Type: model.Next}
	other.CommunicationChannel <- model.Msg{Type: model.Next}
	sum := (<-chooser.PlayerMoveChannel).Value + (<-other.PlayerMoveChannel).Value

	loser, winner := other, chooser
	if sum%2 != guess.Value {
		loser, winner = chooser, other
	}

	loser.CommunicationChannel <- model.Msg{Type: model.Lose}
	result <- winner
}
