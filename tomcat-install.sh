#!/bin/bash
sudo = $(id -u)
if [$sudo -eq 0]
then
  echo "You Are In Root User"
else
  echo "You Are Not The Root User , Please Go To The Root User"
if