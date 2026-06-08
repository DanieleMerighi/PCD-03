package main

import "sync"

func main() {
	const m = 5

	var wg sync.WaitGroup
	wg.Add(1)
	go Organizer(m, &wg)
	wg.Wait()

}
