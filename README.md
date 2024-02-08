# L2 network switch simulation

- By: BEng. Grzegorz Jaskuła, BEng. Adam Rektor
- Supervision: Prof. Maciej Stasiak, Dsc. Slawomir Hanczewski

Poznań University of Technology, 2023-2024

**This program is for educational purposes only and is provided as it is with absolutely no warranty.** As there is no detailed public documentation refering to hardware and way it exactly operates it is impossible to recreate the L2 switch with 100% accuracy. Therefore this software is mathematical model simulating an abstract device. Some results may differ from real networking equipment.

<img width="1299" alt="Screenshot 2024-01-13 at 13 21 34" src="https://github.com/gjaskula99/L2-network-switch-simulation/assets/81091594/c1eb0f07-d113-455e-974e-c80efb7452f8">

***

## About

This program is simulating layer 2 network switch with 8 ethernet interfaces. Each interface has its own traffic source which can be set to different probabilistic distributions. It's main goal is to calculate ammonut of traffic being lost and corresponding probability.

- Traffic generated between 2-8 interfaces
- Fixed and varied frames length
- Store and forward and cut through switching modes
- Adjustable speed to set how much data to proccess in each iteration
- Adjustable frame handling time and ammount of broadcast traffic
- Adjustable minimal time between frames and various adjustable traffic generators
- Statistics display including: number of frames received/transmitted/lost/transmitted broadcast, lost percentage, data transfer in/out, CAM (MAC) table view, interface buffer view
- Adjustable buffers and CAM (MAC) table size
- MAC flooding mode to demonstrate CAM table attack
- Ability to read settings from configfile
- Image of switch with flashing LEDs

Simulator is run for time specified in seconds. **Time given refers to REAL COMPUTING TIME, not switch running time** to ensure it will not take ethernity to complete. It means running speed may differ depending on hardware. Simulation can be resumed with keeping current results. Button *HALT* is supposed to stop simulator in emergency - use with caution as it violently kills the thread.

Detailed information are provided on [wiki](https://github.com/gjaskula99/L2-network-switch-simulation/wiki).
