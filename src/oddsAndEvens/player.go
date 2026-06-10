package main

import "math/rand"

const MAX_VALUE_MOVE = 6
const MAX_VALUE_CHOISE = 2

func player(communication chan Msg, move chan Move) {
	for {
		msg := <-communication //io mi blocco qui e aspetto i messaggi
		switch msg.Type {
		case Next:
			move <- Move{
				Value: rand.Intn(MAX_VALUE_MOVE),
			}
		case Lose:
			return
		case Choise:
			move <- Move{
				Value: rand.Intn(MAX_VALUE_CHOISE),
			}
		}
	}
}

func spawnPlayer(communication chan Msg) chan Move {
	move := make(chan Move)
	go player(communication, move)
	return move
}
