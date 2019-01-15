for dir in log/*/
do
    python3 main.py --do_train true --num_epochs 10 --sub $dir
done