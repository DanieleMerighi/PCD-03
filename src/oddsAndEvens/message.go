package main

const (
	Lose Type = iota + 1
	Next
	Choise
)

type Type int

type Move struct {
	Value int
}

type Msg struct {
	Type Type
}
