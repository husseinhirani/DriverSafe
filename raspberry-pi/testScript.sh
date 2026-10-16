#!/bin/bash

while true; do
	rpicam-jpeg -o test.jpg

	output=$(python3 label_image.py \
	  --model_file model_unquant.tflite \
	  --label_file labels.txt \
	  --image test.jpg | tee myfile.txt)
	
	echo $output > recordFile.txt
	

	rm test.jpg
done
