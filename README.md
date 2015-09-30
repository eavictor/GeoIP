# GeoIP
a Tiny project for generate MikroTik RouterOS readable IP address-list.

IPv6 prefix calculation reference: https://github.com/janvanbesien/java-ipv6

janvanbesien, thank you :)



RESTful API example:

@ generate IPv6 only list for TW, JP and US: http://url:port/GeoIP/api/ip/v6/TW,JP,US

@ generate IPv4 only list for TW, JP and US: http://url:port/GeoIP/api/ip/v4/TW,JP,US

@ generate both IPv6 and IPv4 list for TW, JP and US: http://url:port/GeoIP/api/ip/both/TW,JP,US
