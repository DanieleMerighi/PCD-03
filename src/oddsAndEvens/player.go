package main

import "math/rand"

const MAX_VALUE_MOVE = 6
const MAX_VALUE_CHOICE = 2

func player(communication chan Msg, move chan Move) {
	for {
		msg, ok := <-communication
		if !ok {
			return
		}
		switch msg.Type {
		case Next:
			move <- Move{
				Value: rand.Intn(MAX_VALUE_MOVE),
			}
		case Lose:
			return
		case Choice:
			move <- Move{
				Value: rand.Intn(MAX_VALUE_CHOICE),
			}
		}
	}
}

func spawnPlayer(communication chan Msg) chan Move {
	move := make(chan Move)
	go player(communication, move)
	return move
}
