package main

import (
	"fmt"
	"log"
	"math/rand"
	"sync"
)

type Organization struct {
	PlayerName          string
	PlayerMoveChannel   chan Move
	ComunicationChannel chan Msg
}

func Organizer(m int, wg *sync.WaitGroup) {
	var numberOfPlayer = 1 << m //da capire
	organization := make(map[string]Organization)
	currentlyPlaying := []string{}

	for i := 0; i < numberOfPlayer; i++ {
		playerID := fmt.Sprintf("player-%d", i)
		comunication := make(chan Msg)
		organization[playerID] = Organization{
			PlayerName:          playerID,
			PlayerMoveChannel:   spawnPlayer(comunication),
			ComunicationChannel: comunication,
		}
		currentlyPlaying = append(currentlyPlaying, playerID)
	}

	for i := range currentlyPlaying {
		j := rand.Intn(i + 1)
		currentlyPlaying[i], currentlyPlaying[j] = currentlyPlaying[j], currentlyPlaying[i]
	}

	for round := 1; round <= m; round++ {
		numGames := len(currentlyPlaying) / 2
		var resultChannels []chan string

		log.Printf("Round %d, giocatori %d, games: %d", round, len(currentlyPlaying), numGames)

		for g := 0; g < numGames; g++ {
			a := currentlyPlaying[2*g]
			b := currentlyPlaying[2*g+1]
			result := make(chan string)
			go Referee(organization[a], organization[b], result)
			resultChannels = append(resultChannels, result)
		}

		for i := 0; i < len(resultChannels); i++ {
			looser := <-resultChannels[i]
			delete(organization, looser)
			for j, player := range currentlyPlaying {
				if player == looser {
					currentlyPlaying = append(currentlyPlaying[:j], currentlyPlaying[j+1:]...)
				}
			}
		}

		log.Printf("%v", currentlyPlaying)
	}

	wg.Done()
}
