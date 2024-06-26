
测试依赖顺序

## group 1

- clients/jsonplaceholder
- core/bus
- core/cache
- script

## group 2

- handlers/jsonplaceholder 
    + clients/jsonplaceholder
    + script
- stompchannel
    + core/bus
- jms
    + core/bus
    + script

## group 3

- zhiyoufyworker
    + core/bus
    + jms
