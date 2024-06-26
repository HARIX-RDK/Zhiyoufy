if [ "$#" -ne 1 ]; then
    echo "usage: $0 NAMESPACE"
    exit 1
fi


echo "kubectl -n $1 get pods -o jsonpath="{..image}" |tr -s '[[:space:]]' '\n' |sort |uniq -c"
kubectl -n $1 get pods -o jsonpath="{..image}" |tr -s '[[:space:]]' '\n' |sort |uniq -c
