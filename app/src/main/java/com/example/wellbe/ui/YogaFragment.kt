package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R

data class Yoga(val name: String, val gifRes: Int, val benefits: String)

class YogaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_yoga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerYoga)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val yogaList = listOf(
            Yoga("Tree Pose (Vrikshasana)", R.drawable.tree_pose, "Improves balance, strengthens legs and back."),
            Yoga("Downward Dog (Adho Mukha Svanasana)", R.drawable.downward_dog, "Strengthens arms and legs, stretches spine."),
            Yoga("Cobra Pose (Bhujangasana)", R.drawable.cobra_pose, "Opens chest, strengthens spine, reduces stress."),
            Yoga("Child’s Pose (Balasana)", R.drawable.child_pose, "Relaxes body, reduces fatigue, stretches back."),
            Yoga("Warrior Pose (Virabhadrasana)", R.drawable.warrior_pose, "Strengthens legs, improves focus and stability."),
            Yoga("Bridge Pose (Setu Bandhasana)", R.drawable.bridge_pose, "Strengthens back, glutes, and improves digestion.")
        )

        recycler.adapter = YogaAdapter(yogaList)
    }
}
