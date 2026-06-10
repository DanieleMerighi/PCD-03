package main

import (
	"math/rand"
)

func Referee(playerOneOrganization Organization, playerTwoOrganization Organization, result chan string) {
	choosenOne := rand.Intn(2) + 1
	var choise Move
	if choosenOne == 1 {
		playerOneOrganization.ComunicationChannel <- Msg{
			Type: Choise,
		}
		choise = <-playerOneOrganization.PlayerMoveChannel
	} else {
		playerTwoOrganization.ComunicationChannel <- Msg{
			Type: Choise,
		}
		choise = <-playerTwoOrganization.PlayerMoveChannel
	}

	playerOneOrganization.ComunicationChannel <- Msg{
		Type: Next,
	}
	playerTwoOrganization.ComunicationChannel <- Msg{
		Type: Next,
	}

	moveOne := <-playerOneOrganization.PlayerMoveChannel
	//log.Printf("[referee] recived move %d, from player %s", moveOne.Value, playerOneOrganization.PlayerName)
	moveTwo := <-playerTwoOrganization.PlayerMoveChannel
	//log.Printf("[referee] recived move %d, from player %s", moveTwo.Value, playerTwoOrganization.PlayerName)

	//log.Printf("%d, %d", choise.Value, choosenOne)
	if (moveOne.Value+moveTwo.Value)%2 == choise.Value {
		if choosenOne == 1 {
			playerTwoOrganization.ComunicationChannel <- Msg{
				Type: Lose,
			}
			result <- playerTwoOrganization.PlayerName
		} else {
			playerOneOrganization.ComunicationChannel <- Msg{
				Type: Lose,
			}
			result <- playerOneOrganization.PlayerName
		}
	} else {
		if choosenOne == 1 {
			playerOneOrganization.ComunicationChannel <- Msg{
				Type: Lose,
			}
			result <- playerOneOrganization.PlayerName
		} else {
			playerTwoOrganization.ComunicationChannel <- Msg{
				Type: Lose,
			}
			result <- playerTwoOrganization.PlayerName
		}
	}
}
