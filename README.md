# non-zero-check

Takes two values as input and checks whether they are all zero. Sets the timestamp to the one from the last message

## Inputs

* value1 (float): First input
* value2 (float). Second input
* timestamp (string): Timestamp from device

## Outputs

* value (int): Is 1 if all inputs are greater than 0. Is 0 if at least one of the inputs is equal to 0.
* lastTimestamp (string): timestamp of last message
