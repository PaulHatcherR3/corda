FROM azul/zulu-openjdk:11
ARG USER="buildUser"
ARG UID=999
ARG GID=999
RUN groupadd -g ${GID} -o ${USER}
RUN useradd -o -m -u ${UID} -g ${GID} ${USER}
USER ${USER}