./gradlew bootBuildImage \
  --imageName ghcr.io/polarbookshop-kotlin/order-service \
  --publishImage \
  -PregistryUrl=ghcr.io \
  -PregistryUsername=polarbookshop-kotlin \
  -PregistryToken="$GHCR_TOKEN" \
  --builder dashaun/builder:tiny