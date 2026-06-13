package actors

import (
	"math/rand"
	"oddsAndEvens/internal/model"
)

const MAX_VALUE_MOVE = 6
const MAX_VALUE_CHOICE = 2

func player(communication chan model.Msg, move chan model.Move) {
	for {
		msg, ok := <-communication
		if !ok {
			return
		}
		switch msg.Type {
		case model.Next:
			move <- model.Move{
				Value: rand.Intn(MAX_VALUE_MOVE),
			}
		case model.Lose:
			return
		case model.Choice:
			move <- model.Move{
				Value: rand.Intn(MAX_VALUE_CHOICE),
			}
		}
	}
}

func spawnPlayer(communication chan model.Msg) chan model.Move {
	move := make(chan model.Move)
	go player(communication, move)
	return move
}
