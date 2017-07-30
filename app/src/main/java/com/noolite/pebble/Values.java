package com.noolite.pebble;

import com.noolite.NooLiteDefs;
import com.noolite.domain.ChannelElement;
import com.noolite.groups.GroupElement;
import com.noolite.groups.SensorElement;

import java.util.ArrayList;
import java.util.List;

public class Values {

    private static int groupIndex = -1;
    private static GroupElement currentGroup;

    private static int channelIndex = -1;
    private static List<ChannelElement> channelsToView = new ArrayList<ChannelElement>();

    public static void clear() {
        groupIndex = -1;
        currentGroup = null;
        channelIndex = -1;
        channelsToView.clear();
    }



    public static GroupElement nextGroup(List<GroupElement> gpoups) {
        if (gpoups.isEmpty()) {
            return null;
        }
        groupIndex++;

        if (groupIndex >= gpoups.size()) {
            groupIndex = 0;
        }
        GroupElement gr = gpoups.get(groupIndex);
        currentGroup = gr;
        return gr;
    }

    public static GroupElement getGroup() {
        return currentGroup;
    }

    public static void setChannelsToView(GroupElement gr) {
        channelsToView.clear();
        channelsToView.addAll(gr.getChannelElements());

        //добавление каналов, которые надо отображать
        for (SensorElement sensorElement : gr.getSensorElements()) {
            channelsToView.add(
                    new ChannelElement(sensorElement.getId(), sensorElement.getName(), NooLiteDefs.CHANNEL_TYPE_SENSOR, 0, 0));
        }
        channelIndex = -1;
    }

    public static ChannelElement nextChannel() {
        if (channelsToView.isEmpty()) {
            return null;
        }
        channelIndex++;

        if (channelIndex >= channelsToView.size())  {
            channelIndex = 0;
        }

        return channelsToView.get(channelIndex);
    }

    public static ChannelElement getChannel() {
        if (channelsToView.isEmpty()) {
            return null;
        }
        return channelsToView.get(channelIndex);
    }

    public static int totalCountOfGroups;
	public static int indexOfCurrentGroup;
	public static int totalCountOfChannels;
	public static int totalCountOfDetectors;
	public static int indexOfCurrentChannel;
	public static int typeOfCurrentChannel;
	public static int count;

}
