package actors

import (
	"fmt"
	"math/rand"
	"oddsAndEvens/internal/model"
)

// namesLimit is the field size below which we print player names instead of
// just counts, so the late rounds stay readable without flooding the output.
const NAMES_LIMIT = 8

type Organization struct {
	PlayerName           string
	PlayerMoveChannel    chan model.Move
	CommunicationChannel chan model.Msg
}

func Organizer(m int) {
	var numberOfPlayer = 1 << m // 2^m players
	currentlyPlaying := []Organization{}

	for i := range numberOfPlayer {
		playerID := fmt.Sprintf("player-%d", i)
		communication := make(chan model.Msg)
		currentlyPlaying = append(currentlyPlaying, Organization{
			PlayerName:           playerID,
			PlayerMoveChannel:    spawnPlayer(communication),
			CommunicationChannel: communication,
		})
	}

	rand.Shuffle(len(currentlyPlaying), func(i, j int) {
		currentlyPlaying[i], currentlyPlaying[j] = currentlyPlaying[j], currentlyPlaying[i]
	})

	fmt.Printf("Tournament: %d players, %d rounds\n\n", numberOfPlayer, m)

	for round := 1; round <= m; round++ {
		numGames := len(currentlyPlaying) / 2
		printRound(round, currentlyPlaying, numGames)

		results := make([]chan Organization, numGames)
		for g := range numGames {
			a := currentlyPlaying[2*g]
			b := currentlyPlaying[2*g+1]
			results[g] = make(chan Organization)
			go Referee(a, b, results[g])
		}

		winners := []Organization{}
		for g := range numGames {
			winners = append(winners, <-results[g])
		}
		currentlyPlaying = winners
	}

	fmt.Printf("\nChampion: %s\n", currentlyPlaying[0].PlayerName)

	// tournament over: close the survivor's channel so its goroutine can exit
	for _, p := range currentlyPlaying {
		close(p.CommunicationChannel)
	}
}

// printRound logs one line per round: a name list when few players remain,
// otherwise just the headcount and number of games.
func printRound(round int, players []Organization, numGames int) {
	if len(players) <= NAMES_LIMIT {
		fmt.Printf("Round %d: %d players -> %v\n", round, len(players), playerNames(players))
	} else {
		fmt.Printf("Round %d: %d players, %d games\n", round, len(players), numGames)
	}
}

func playerNames(players []Organization) []string {
	names := make([]string, len(players))
	for i, p := range players {
		names[i] = p.PlayerName
	}
	return names
}
