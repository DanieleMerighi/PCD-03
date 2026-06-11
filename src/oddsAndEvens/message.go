package main

type Type int

const (
	Lose Type = iota + 1
	Next
	Choice
)

type Move struct {
	Value int
}

type Msg struct {
	Type Type
}
