# L2 network switch simulation

- By: BEng. Grzegorz Jaskuła, BEng/ Adam Rektor
- Supervision: Prof. Maciej Stasiak, Phd. Slawomir Hanczewski

Poznań University of Technology, 2023

**This program is for educational purposes only and is provided as it is with absolutely no warranty.**

<img width="454" alt="simulator_gui" src="https://github.com/gjaskula99/L2-network-switch-simulation/assets/81091594/45a13f8f-c2dd-4cea-95ac-7b5490cbf9ff">

***

## About

This program is simulating layer 2 network switch with 8 ethernet interfaces. Each interface has its own traffic source which can be set to different probabilistic distributions. It's main goal is to calculate ammonut of traffic being lost and corresponding probability.

- Traffic generated between 2-8 interfaces
- Fixed and varied frames length
- Store and forward and cut through switching modes
- Adjustable frame handling time
- Adjustable minimal time between frames
- Statistics display including: number of frames received/transmitted/lost, lost percentage, data transfer in/out, CAM (MAC) table view, interface buffer view
- Image of switch with flashing LEDs
- More features will be added in the future

Simulator is run for time specified in seconds. **Time given refers to REAL COMPUTING TIME, not switch running time**. It means running speed may differ depending on hardware. Simulation can be resumed in the same way. Button *HALT* is supposed to stop simulator in emergency - use with caution as it violently kills the thread.

Detailed information are provided on [wiki](https://github.com/gjaskula99/L2-network-switch-simulation/wiki).
